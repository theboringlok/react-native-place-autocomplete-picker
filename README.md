# React Native Place Autocomplete Picker

A fully native, high-performance and easy to use Google Place Autocomplete picker for React Native. Built with the new architecture (Turbo Modules) and the **new Google Places SDK** for maximum performance and a seamless native feel on both iOS and Android.

## Demos

| iOS | Android |
| :----: | :----: |
| <img src="assets/images/iOSPlaceAutoCompletePicker.gif" width="240"/> | <img src="assets/images/androidPlaceAutoCompletePicker.gif" width="240"/> |

## ✨ Features

- **📍 New Google Places SDK**: Utilizes the latest and most powerful Google Places SDK for accurate and reliable place data.
- **📱 Fully Native UI**: Presents a 100% native autocomplete interface on both iOS and Android for a smooth user experience.
- **🔧 Customizable**: Control the presentation mode on Android (fullscreen or overlay).
- **🚀 Turbo Module Powered**: Built for the new React Native architecture, ensuring high performance and direct native integration.
- **📝 Rich Data**: Returns detailed place information, including formatted address, coordinates, and granular `addressComponents` (with long and short names).

## 📦 Installation

Install the package using npm or yarn:

```bash
npm install react-native-place-autocomplete-picker
# --- or ---
yarn add react-native-place-autocomplete-picker
```

## iOS Setup

Install pods:

```bash
npx pod-install
# --- or ---
cd ios && pod install
```

## Android Setup

No additional setup is required for Android. The API key is passed programmatically. Just rebuild the project and run.

### 🔑 Getting Your API Key

This module uses the new Google Places SDK, which requires you to enable the **"Places API (New)"** in your Google Cloud project. You can use an existing API key, but you must ensure this new service is enabled for it.

1. Go to the [Google Cloud Console](https://console.cloud.google.com/).
2. Create a new project or select an existing one.
3. **Enable Billing** for your project and **APIs & Services > Library**.
4. Search for **"Places API (New)"**.
    > **Important:** Do not select the legacy "Places API". This module will not work with it.
5. Click the **Enable** button and go to **APIs & Services > Credentials** to create a new API Key or select an existing one.

### 🔑 Restricting Your API Key

You can restrict the API keys for both iOS and Android by adding platform-specific conditions, as demonstrated in the App.tsx file. For security, it's crucial to restrict your key. Go to the Application restrictions section—add the iOS restriction using your app's Bundle ID and the Android restriction using your app's Package Name and SHA-1 fingerprint. In the API restrictions section, ensure that the key has access only to the Places API.

## Usage

Using the picker is straightforward. First, initialize it with your API key as soon as your app starts, then call the open method to present the UI.

```javascript
import { View, Button, Text, StyleSheet, ScrollView, Platform } from 'react-native';
import * as PlacePicker from 'react-native-place-autocomplete-picker';

// It's recommended to store your API key in a secure way, e.g., environment variables.
const iOS_API_KEY = "YOUR_GOOGLE_PLACES_API_KEY";
const ANDROID_API_KEY = "YOUR_GOOGLE_PLACES_API_KEY";

export default function App() {
  const [selectedPlace, setSelectedPlace] = useState(null);

  useEffect(() => {
    // Initialize the module once, e.g., in your root component
    // This connects to the new Places SDK.
    const YOUR_API_KEY = Platform.os === "android"?ANDROID_API_KEY:iOS_API_KEY
    PlacePicker.initialize(YOUR_API_KEY);
  }, []);

  const handleOpenPicker = async (mode) => {
    try {
      const place = await PlacePicker.open(mode);
      console.log(JSON.stringify(place, null, 2));
      setSelectedPlace(place);
    } catch (error) {
      // Handle cancellation or other errors
      console.log(error.message);
    }
  };

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <View style={styles.buttonContainer}>
        <Button title="Open Picker (Fullscreen)" onPress={() => handleOpenPicker('fullscreen')} />
      </View>
      {Platform.OS === 'android' && (
        <View style={styles.buttonContainer}>
          <Button title="Open Picker (Overlay)" onPress={() => handleOpenPicker('overlay')} />
        </View>
      )}

      {selectedPlace && (
        <View style={styles.placeDetails}>
          <Text style={styles.placeName}>{selectedPlace.name}</Text>
          <Text>{selectedPlace.address}</Text>
          <Text>Lat: {selectedPlace.latitude}, Lng: {selectedPlace.longitude}</Text>
          {selectedPlace.postalCode && <Text>Postal Code: {selectedPlace.postalCode}</Text>}
        </View>
      )}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    padding: 20,
  },
  buttonContainer: {
    marginBottom: 10,
  },
  placeDetails: {
    marginTop: 20,
    padding: 15,
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 8,
  },
  placeName: {
    fontWeight: 'bold',
    fontSize: 16,
    marginBottom: 5,
  },
});
```

## API Reference

`initialize(apiKey)`
Initializes the Google Places SDK with your API key. This must be called once before using the open method. Ensure the API key is associated with a project that has the Places API (New) enabled.

`open(mode?)`
Opens the native place picker UI. Returns a promise that resolves with the selected place object or rejects if the user cancels or an error occurs.

Returns: `Promise<Place>`

The promise resolves with a Place object with the following structure:

```typescript
interface Place {
  name: string;
  address: string;
  latitude: number;
  longitude: number;
  postalCode?: string;
  addressComponents?: AddressComponent[];
}

interface AddressComponent {
  longName: string;
  shortName?: string;
  types: string[];
}
```

## 🤝 Contributing

Contributions are always welcome! Whether it's bug reports, feature requests, or pull requests, please feel free to contribute.

1. Fork the Project
2. Create your Feature Branch (git checkout -b feature/AmazingFeature)
3. Commit your Changes (git commit -m 'feat: Add some AmazingFeature')
4. Push to the Branch (git push origin feature/AmazingFeature)
5. Open a Pull Request

### 📜 License

This project is licensed under the MIT License
