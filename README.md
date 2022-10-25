### Test Cases
https://docs.google.com/spreadsheets/d/1XyfwCmcLufVoxVsiCoDKx1nvzsQmlw5ZhkCbDQdJu7o/edit?usp=sharing

### Config
#### _config.properties
```
DEV.user_id={GROUP_ADMIN_USER_ID}
DEV.group_id={GROUP_ID}
```
#### local.properties
Resource in classpath. Loaded in GroupsAPITest for api urlParam
```
access_token={USER_ACCESS_TOKEN}
```

### API Reference
https://developers.facebook.com/docs/groups-api/reference

### Run tests
```
mvn clean test -Dsuite=api
```

