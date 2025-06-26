import PlaceAutocompletePicker, {
  type PickerMode,
  type Place,
  type PlaceAddressComponent,
} from './NativePlaceAutocompletePicker';

export function initialize(apiKey: string): void {
  if (!apiKey) {
    console.warn('API key is null or empty.');
    return;
  }
  PlaceAutocompletePicker.initialize(apiKey);
}

export function open(mode?: PickerMode): Promise<Place> {
  return PlaceAutocompletePicker.open(mode);
}

export type { Place, PlaceAddressComponent };
