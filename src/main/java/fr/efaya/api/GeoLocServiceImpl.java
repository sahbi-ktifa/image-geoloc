package fr.efaya.api;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.GpsDescriptor;
import com.drew.metadata.exif.GpsDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class GeoLocServiceImpl implements GeoLocService {
    private Logger logger = LoggerFactory.getLogger(GeoLocServiceImpl.class);

    @Override
    public void verify(File file) throws BadGeolocationException {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(file);
            GpsDirectory directory = metadata.getFirstDirectoryOfType(GpsDirectory.class);
            if (directory == null) {
                logger.error("No geolocation information");
                throw new BadGeolocationException();
            }
            GpsDescriptor descriptor = new GpsDescriptor(directory);
            if (descriptor.getGpsLongitudeDescription() == null
                    || descriptor.getGpsLatitudeDescription() == null
                    || isLocationUnacceptable(descriptor.getGpsLongitudeDescription(), descriptor.getGpsLatitudeDescription())) {
                logger.error("Incorrect geolocation information");
                throw new BadGeolocationException();
            }
        } catch (IOException | ImageProcessingException e) {
            logger.error("An error occured : " + e.getMessage());
            throw new BadGeolocationException();
        }
    }

    private boolean isLocationUnacceptable(String longitude, String latitude) {
        Double lat = Double.valueOf(latitude);
        if (lat < 1.77 || lat > 1.81) {
            return true;
        }
        Double lon = Double.valueOf(longitude);
        if (lon < 48.84 || lon > 48.88) {
            return true;
        }
        return false;
    }
}
