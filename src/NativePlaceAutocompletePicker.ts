import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

export interface PlaceAddressComponent {
  name: string;
  shortName: string;
  types: string[];
}

export interface Place {
  name: string;
  address: string;
  latitude: number;
  longitude: number;
  postalCode?: string;
  addressComponents?: PlaceAddressComponent[];
}

export type PickerMode = 'fullscreen' | 'overlay';

export interface PickerOptions {
  mode?: PickerMode;
  countries?: string[];
}

export interface Spec extends TurboModule {
  initialize(apiKey: string): void;
  open(options: Object): Promise<Place>;
}

export default TurboModuleRegistry.getEnforcing<Spec>(
  'PlaceAutocompletePicker'
);
