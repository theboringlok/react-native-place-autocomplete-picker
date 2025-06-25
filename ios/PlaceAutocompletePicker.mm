#import "PlaceAutocompletePicker.h"

@implementation PlaceAutocompletePicker
{
  RCTPromiseResolveBlock _resolve;
  RCTPromiseRejectBlock _reject;
}

RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(initialize:(NSString *)apiKey)
{
  dispatch_async(dispatch_get_main_queue(), ^{
    [GMSPlacesClient provideAPIKey:apiKey];
  });
}

- (void)open: (NSString *)mode resolve:(nonnull RCTPromiseResolveBlock)resolve reject:(nonnull RCTPromiseRejectBlock)reject {
  

  dispatch_async(dispatch_get_main_queue(), ^{
    
    self->_resolve = resolve;
    self->_reject = reject;
    
    GMSAutocompleteViewController *acController = [[GMSAutocompleteViewController alloc] init];
    acController.delegate = self;

    // Specify the place data to return.
    GMSPlaceField fields = (GMSPlaceFieldName | GMSPlaceFieldPlaceID | GMSPlaceFieldCoordinate | GMSPlaceFieldFormattedAddress | GMSPlaceFieldAddressComponents | GMSPlaceFieldTypes);
    acController.placeFields = fields;
    
    UIViewController *rootViewController = [UIApplication sharedApplication].delegate.window.rootViewController;
    while (rootViewController.presentedViewController) {
            rootViewController = rootViewController.presentedViewController;
    }
    [rootViewController presentViewController:acController animated:YES completion:nil];
  });
}

// Handle the user's selection.
- (void)viewController:(GMSAutocompleteViewController *)viewController
didAutocompleteWithPlace:(GMSPlace *)place {
  [viewController.presentingViewController dismissViewControllerAnimated:YES completion:nil];

  if (_resolve) {
    NSString *postalCode = @"";
    NSMutableArray *addressComponentsArray = [NSMutableArray array];
        if (place.addressComponents) {
            for (GMSAddressComponent *component in place.addressComponents) {
                NSMutableDictionary *componentDict = [NSMutableDictionary dictionary];
                componentDict[@"name"] = component.name;
                componentDict[@"shortName"] = component.shortName;
                componentDict[@"types"] = component.types;
                [addressComponentsArray addObject:componentDict];
                
                // Keep postal code extraction for convenience
              if ([component.types containsObject:kGMSPlaceTypePostalCode]) {
                    postalCode = component.name;
                }
            }
        }
    
    NSDictionary *placeData = @{
      @"name": place.name ?: @"",
      @"address": place.formattedAddress ?: @"",
      @"latitude": @(place.coordinate.latitude),
      @"longitude": @(place.coordinate.longitude),
      @"postalCode": postalCode,
      @"addressComponents": addressComponentsArray
    };
    
    _resolve(placeData);
    _resolve = nil;
    _reject = nil;
  }
}

- (void)viewController:(GMSAutocompleteViewController *)viewController
didFailAutocompleteWithError:(NSError *)error {
  [viewController.presentingViewController dismissViewControllerAnimated:YES completion:nil];
  if (_reject) {
    _reject(@"E_AUTOCOMPLETE_ERROR", [error localizedDescription], error);
    _resolve = nil;
    _reject = nil;
  }
}

// User canceled the operation.
- (void)wasCancelled:(GMSAutocompleteViewController *)viewController {
  [viewController.presentingViewController dismissViewControllerAnimated:YES completion:nil];
  if (_reject) {
    _reject(@"E_PICKER_CANCELLED", @"Picker was cancelled", nil);
    _resolve = nil;
    _reject = nil;
  }
}

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    return std::make_shared<facebook::react::NativePlaceAutocompletePickerSpecJSI>(params);
}

@end
