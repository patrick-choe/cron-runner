# Cron Runner

## Description / 설명

Simple cron command runner for Bukkit 1.17+

Bukkit 1.17 이상을 위한 간단한 cron 명령어 실행기 입니다.

## Configuration Guide (config.yml)

```
debug: (optional) whether to print debug information (when reading & executing), defaults to false
cron-type: (optional) cron type to use. Available types: UNIX (default), CRON4J, QUARTZ, SPRING
tasks: (required) list of tasks to execute
  * On each task section
  - cron: (required) list of cron expressions, or a single cron expression
  - run-once: (optional) whether to run cron once after startup/reload, defaults to false
  - command: (required) list of minecraft commands, or a single minecraft command (do not use slash in front of command)
```

## 설정 가이드 (config.yml)

```
debug: (선택사항) 설정 파일을 읽을 때 및 명령어 실행시에 콘솔에 디버그 정보 출력 여부, 기본값: false
cron-type: (선택사항) 사용할 cron 종류. 사용 가능한 종류: UNIX (기본값), CRON4J, QUARTZ, SPRING
tasks: (필수 사항) 실행할 작업 목록
  * 작업별 설정 
  - cron: (필수 사항) 사용할 단일, 혹은 여러 cron 표현식 
  - run-once: (선택사항) 서버 시작/리로드 후 한번만 실행할지 여부, 기본값: false
  - command: (필수 사항) 실행할 단일, 혹은 여러 마인크래프트 커맨드 (앞에 슬래시를 붙이지 않습니다)
```

## Example Configuration / 예시 설정
```yaml
debug: true
cron-type: UNIX
tasks:
  - cron: "* * * * *"
    command:
      - "say Hello World!"
      - "say Goodbye World!"
  - cron:
    - "0 12 * * *"
    - "0 18 * * *"
    command: "stop"
  - cron: "0 12 * * *"
    run-once: true
    command: "say one time event"
```