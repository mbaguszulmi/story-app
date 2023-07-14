# StoryApp

1. SharedElement animation. Implemented when navigating from MainActivity to DetailActivity.
2. Save login token with DataStore.
3. Use EventBus for Authentication and UI State handling.
4. Developed with Bottom - Up approach.
5. Enable optional location when adding a story.
6. Custom map style.
7. Paging 3 With RemoteMediator
8. Techs:
   - MVVM (Android Architecture Component)
   - Data Binding
   - Flow
   - Hilt
   - Glide
   - Retrofit2
   - Datastore
   - Timber
   - Room
   - GoogleMaps
   - Mockito

## Setup

- Add this to `local.properties` for Google Maps API Key integration
    ```
    MAPS_API_KEY=<YOUR_API_KEY>
    ```

- Add this to `local.properties` to enable Randomize Location (User location will be randomized)
    ```
    USE_RANDOMIZE_LOCATION=true
    ```
