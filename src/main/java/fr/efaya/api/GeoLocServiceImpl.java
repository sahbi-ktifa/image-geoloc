package fr.efaya.api;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.GpsDescriptor;
import com.drew.metadata.exif.GpsDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GeoLocServiceImpl implements GeoLocService {
    private Logger logger = LoggerFactory.getLogger(GeoLocServiceImpl.class);

    private static double[] BOUNDARY_GEO_LAT_MAX = {48.0, 52.0, 0.0};
    private static double[] BOUNDARY_GEO_LAT_MIN = {48.0, 50.0, 0.0};
    private static double[] BOUNDARY_GEO_LON_MAX = {1.0, 49.0, 0.0};
    private static double[] BOUNDARY_GEO_LON_MIN = {1.0, 45.0, 0.0};

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
        List<Double> lat = resolveCoordinate(latitude);
        if (checkCoordinateOut(lat, BOUNDARY_GEO_LAT_MIN, BOUNDARY_GEO_LAT_MAX)) {
            return true;
        }
        List<Double> lon = resolveCoordinate(longitude);
        if (checkCoordinateOut(lon, BOUNDARY_GEO_LON_MIN, BOUNDARY_GEO_LON_MAX)) {
            return true;
        }
        return false;
    }

    private boolean checkCoordinateOut(List<Double> coordinate, double[] refMin, double[] refMax) {
        if (CollectionUtils.isEmpty(coordinate) || coordinate.size() < 3) {
            return true;
        }
        if (coordinate.get(0) < refMin[0] || coordinate.get(0) > refMax[0]) {
            return true;
        }
        if (coordinate.get(1) < refMin[1] || coordinate.get(1) > refMax[1]) {
            return true;
        }
        return false;
    }

    private List<Double> resolveCoordinate(String coordinate) {
        String[] split = coordinate.split(" ");
        return Stream.of(split)
                .map(d -> Double.valueOf(d.replaceAll("[^\\d.]", "")))
                .collect(Collectors.toList());
    }
}
