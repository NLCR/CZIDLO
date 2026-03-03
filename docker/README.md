### Start
```shell
docker compose up -d
```
Open `http://localhost:9300/` in your web browser.

## Stop
```shell
docker compose down
```

## Rebuild and restart service

```shell
#force restart service
docker compose up -d --force-recreate 
```