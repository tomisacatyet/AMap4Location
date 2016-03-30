//
//  CDVAMapLocation.m
//  Created by tomisacat on 16/1/8.
//
//

#import "CDVAMap4Location.h"

@implementation CDVAMap4Location


//readValueFrom mainBundle
-(NSString *)getAMapApiKey{
    return [[[NSBundle mainBundle]infoDictionary]objectForKey:@"AMapApiKey"];
}

//init Config
-(void) initConfig{
    if(!self.locationManager){
        //set APIKey
        [AMapLocationServices sharedServices].apiKey = [self getAMapApiKey];
        //init locationManager
        self.locationManager = [[AMapLocationManager alloc]init];
        //set DesiredAccuracy
        [self.locationManager setDesiredAccuracy:kCLLocationAccuracyHundredMeters];
    }
}


//location method
-(void) location:(CDVInvokedUrlCommand*)command{
    [self initConfig];

    [self.commandDelegate runInBackground:^{
        [self.locationManager requestLocationWithReGeocode:YES completionBlock:^(CLLocation *location, AMapLocationReGeocode *regeocode, NSError *error) {
            CDVPluginResult* pluginResult = nil;
            if (error)
            {

                if (error.code == AMapLocationErrorLocateFailed)
                {
                NSString *errorCode = [NSString stringWithFormat: @"%ld", (long)error.code];
                NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:
                                      errorCode,@"errorCode",
                                      error.localizedDescription,@"errorInfo",
                                      nil];
                    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:dict];
                    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                }
            }else{
                if (regeocode)
                {
                    NSNumber *latitude = [[NSNumber alloc] initWithDouble:location.coordinate.latitude];
                    NSNumber *longitude = [[NSNumber alloc] initWithDouble:location.coordinate.longitude];
                    NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:
                                            regeocode.formattedAddress,@"formattedAddress",
                                            latitude,@"latitude",
                                            longitude,@"longitude",
                                            regeocode.citycode,@"cityCode",
                                            regeocode.adcode,@"adcode",
                                            regeocode.street,@"street",
                                            regeocode.number,@"number",
                                            regeocode.province,@"provinceName",
                                            regeocode.city,@"cityName",
                                            regeocode.district,@"districtName",
                                            regeocode.township,@"roadName",
                                            regeocode.building,@"building",
                                            regeocode.neighborhood,@"neighborhood",
                    					  nil];
                    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:dict];
                    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                }
            }
        }];
    }];
}


@end
