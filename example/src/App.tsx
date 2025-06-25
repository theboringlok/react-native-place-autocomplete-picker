import { Text, View, StyleSheet } from 'react-native';
import { open,initialize, type Place }  from 'react-native-place-autocomplete-picker';
import { TouchableOpacity } from 'react-native';
import { useState } from 'react';


// Place your Google Places API key here
initialize('API_KEY')

export default function App() {

  const [selectedPlace, setSelectedPlace] = useState<Place|null>(null);

  const getPlaces = async () => {
    try {
      const place = await open();
      console.log('Selected Place:', place);
      setSelectedPlace(place);
    } catch (error) {
      console.error('Error :',error);
    }
  }

  return (
    <View style={styles.container}>
      <TouchableOpacity
        style={{
          backgroundColor: '#6366F1',
          paddingVertical: 14,
          paddingHorizontal: 28,
          borderRadius: 8,
        }}
        onPress={getPlaces}
      >
        <Text style={{ color: '#fff', fontSize: 16, fontWeight: '600' }}>
          Search Places
        </Text>
      </TouchableOpacity>
      {selectedPlace && (
          <View
            style={{
              marginTop: 20,
              backgroundColor: '#f3f4f6',
              borderRadius: 12,
              padding: 18,
              shadowColor: '#000',
              shadowOpacity: 0.08,
              shadowRadius: 8,
              shadowOffset: { width: 0, height: 2 },
              elevation: 2,
              width: 280,
            }}
          >
            <Text style={{ fontSize: 18, fontWeight: '700', color: '#111827', marginBottom: 6 }}>
              {selectedPlace.name}
            </Text>
            <Text style={{ fontSize: 15, color: '#374151', marginBottom: 4 }}>
              {selectedPlace.address}
            </Text>
            <Text style={{ fontSize: 13, color: '#6b7280' }}>
              Latitude: <Text style={{ fontWeight: '600' }}>{selectedPlace.latitude}</Text>
            </Text>
            <Text style={{ fontSize: 13, color: '#6b7280' }}>
              Longitude: <Text style={{ fontWeight: '600' }}>{selectedPlace.longitude}</Text>
            </Text>
            <Text style={{ fontSize: 13, color: '#6b7280', marginTop:10 }}>
              Address components: <Text style={{ fontWeight: '600' }}>{JSON.stringify(selectedPlace.addressComponents)}</Text>
            </Text>
          </View>
        )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
});
