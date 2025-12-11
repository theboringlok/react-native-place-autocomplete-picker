import PlaceAutocompletePicker, {
  type PickerMode,
  type PickerOptions,
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

export function open(options?: PickerOptions): Promise<Place> {
  return PlaceAutocompletePicker.open(options ?? {});
}

export type { Place, PlaceAddressComponent, PickerMode, PickerOptions };
