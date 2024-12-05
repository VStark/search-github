# search-github

This is project for education purposes, it demonstrates usage of some recent technologies for KMP development.

This project as the name says is about searching of the repositories on Github server. It uses REST API for searching functionality.

You can also mark repository with the star which is done by using GraphQL API, so it also demonstrates, how to use these APIs on Github.

To be able to use this application you will need your valid Github token, which you can generate here: https://github.com/settings/tokens

### Tech stack
* Kotlin Multiplatform
* Compose Multiplatform (with Material Design 3)
* Compose navigation
* Jetpack Paging(-compose) 3
* Jetpack Room (as paging cache)
* Ktor 3 (Http client)
* Apollo GraphQL (GraphQL client)

### Limitations

Unfortunately the project is currently aiming only Android platform, I couldn't overcome some incompatibility issues with Room and Paging libraries with other platforms. I would like to add more, but many libraries are curently in Alpha so it also needs some time to get more mature...