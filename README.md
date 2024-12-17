# search-github

This is project for education purposes, it demonstrates usage of some recent technologies for KMP development.

This project as the name says is about searching of the repositories on Github server. It uses REST API for searching functionality.

You can also mark repository with the star which is done by using GraphQL API, so it also demonstrates, how to use these APIs on Github.

To be able to use this application you will need your valid Github token, which you can generate here: https://github.com/settings/tokens.
If you want to also add/remove star on repository you will need to add extra permission `public_repo` to your classic token.

### Tech stack
* Kotlin Multiplatform
* Kotlin Flow
* Compose Multiplatform (with Material Design 3)
* Compose navigation
* Jetpack Paging(-compose) 3
* Jetpack Room (as paging cache)
* Ktor 3 (Http client)
* Apollo GraphQL (GraphQL client)
* MVI architecture

### Limitations

The project currently supports only Android and JVM targets. More targets can be added in the future.