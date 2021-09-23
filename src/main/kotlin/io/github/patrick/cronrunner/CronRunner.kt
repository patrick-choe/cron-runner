/*
 * Copyright (C) 2020 PatrickKR
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Contact me on <mailpatrickkr@gmail.com>
 */

package io.github.patrick.cronrunner

import com.cronutils.model.CronType
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.model.time.ExecutionTime
import com.cronutils.parser.CronParser
import org.bukkit.plugin.java.JavaPlugin
import java.time.ZonedDateTime
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Suppress("unused")
class CronRunner : JavaPlugin() {
    private val scheduler = Executors.newScheduledThreadPool(1)
    private var debug = false

    override fun onEnable() {
        saveDefaultConfig()
        debug = config.getBoolean("debug") as? Boolean == true
        val cronType = CronType.valueOf(config.getString("cron-type") ?: "UNIX")
        val parser = CronParser(CronDefinitionBuilder.instanceDefinitionFor(cronType))

        config.getMapList("tasks").forEach { task ->
            val runOnce = task["run-once"] as? Boolean == true

            val commands = when (val command = task["command"]) {
                is List<*> -> command.map(Any?::toString)
                else -> listOf(command.toString())
            }

            when (val cron = task["cron"]) {
                is List<*> -> cron.map(Any?::toString)
                else -> listOf(cron.toString())
            }.forEach { value ->
                registerTask(runOnce, ExecutionTime.forCron(runCatching {
                    parser.parse(value)
                }.getOrElse { throwable ->
                    throw IllegalArgumentException("Invalid cron (does not match cron definition)", throwable)
                })) {
                    server.scheduler.runTask(this, Runnable {
                        commands.forEach(this::dispatchCommand)
                    })
                }

                if (debug) {
                    logger.info("$value - ${commands.joinToString()}")
                }
            }
        }
    }

    override fun onDisable() {
        scheduler.shutdownNow()
    }

    private fun registerTask(runOnce: Boolean, cron: ExecutionTime, task: () -> Unit) {
        scheduler.schedule({
            task.invoke()

            if (!runOnce) {
                registerTask(false, cron, task)
            }
        }, TimeUnit.NANOSECONDS.convert(cron.timeToNextExecution(ZonedDateTime.now()).get()), TimeUnit.NANOSECONDS)
    }

    private fun dispatchCommand(command: String) {
        if (debug) {
            logger.info("execute /$command")
        }

        server.dispatchCommand(server.consoleSender, command)
    }
}