# Coffee Map

An Android app for finding coffee shops and other places where you can get a coffee. Places are loaded from OpenStreetMap via the Overpass API and displayed on a map. You can save your favorite places, leave reviews, and identify a drink from a photo using ML Kit.

## Features

- **Map of places.** The main screen shows pins for all places where you can buy coffee and more.
- **Place details.** Tapping a pin opens a card with all available information about the place.
- **Favorites.** Any place can be added to your favorites.
- **Reviews.** You can leave a review with a rating for every place.
- **Drink recognition from a photo.** When writing a review, you can take a photo of your drink and the app will identify it (cappuccino, espresso, tea, etc.).
- **Search and filters.** Filter by rating, favorites, and whether a place has reviews.

## Tech Stack

- Android, [Kotlin]
- [Jetpack Compose]
- [Google Maps SDK] for the map
- [Overpass API](https://overpass-api.de/) for fetching place data from OpenStreetMap
- [ML Kit](https://developers.google.com/ml-kit) (Image Labeling) for drink recognition
- [Room] for storing favorites and reviews
- [OkHttp] for networking

Drink recognition requires camera access. The app will ask for permission on first use.

## How It Works

1. The app sends a query to the Overpass API and receives places (cafes, coffee shops, etc.) in the selected area.
2. The results are displayed on the map as pins.
3. When creating a review, the drink photo is passed to ML Kit, and the drink type is filled in automatically based on the model's result.

## Context

University project for the course "[Vývoj aplikací pro Android 2]", Mendel University in Brno, 2025/2026.

## Author

[Anton Simikin]
