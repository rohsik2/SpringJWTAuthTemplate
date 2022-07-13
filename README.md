# SpringJWTAuthTemplate
JWT Auth System Template written in spring

api 통신시 form-data를 request body에 넣은 형태로 통신합니다. 
## Authentication
### POST : /api/oauth/access
Access를 위한 Token을 발행하는 api 입니다. 
~~~
{
    "username"     : { id of user },
    "password"     : { password of user },
}
~~~
Example Response
~~~
{
    "access_token"  : { access token },
    "refresh_token" : { refresh token },
}
~~~

### POST : /api/oauth/refresh
Refresh 된 accessToken을 받기위해서는 아래와 같은 포맷을 form-data로 전송해야 합니다.

Example Request
~~~
{
    "grantType"    : "refresh_token",
    "username"     : { id of user },
    "refreshToken" : { access api를 통해 받은 refesh token }
}
~~~
Example Response
~~~
{
    "access_token"  : { new access token },
    "refresh_token" : { refresh token },
}
~~~

## User Managing Methods
### GET : /api/users
admin을 위한 전용 method입니다. 모든 user의 정보를 반환합니다.

### POST : /api/user
새로운 user를 등록합니다.
Example Request
~~~
{
    "username" : { id of user },
    "password" : { password of user},
    "name"     : { name of user},
}
~~~
Example Response
~~~
{

}
~~~

### GET : /api/user/{username}

