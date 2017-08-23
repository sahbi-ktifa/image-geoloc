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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GeoLocServiceImpl implements GeoLocService {
    private Logger logger = LoggerFactory.getLogger(GeoLocServiceImpl.class);

    @Override
    public Point extractGPSInformation(File file) {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(file);
            GpsDirectory directory = metadata.getFirstDirectoryOfType(GpsDirectory.class);
            if (directory == null) {
                logger.error("No geolocation information");
                throw new BadGeolocationException();
            }
            GpsDescriptor descriptor = new GpsDescriptor(directory);
            if (descriptor.getGpsLongitudeDescription() == null
                    || descriptor.getGpsLatitudeDescription() == null) {
                logger.error("Incorrect geolocation information");
                throw new BadGeolocationException();
            }
            return new Point(resolveCoordinateAsDecimal(descriptor.getGpsLatitudeDescription()),
                    resolveCoordinateAsDecimal(descriptor.getGpsLongitudeDescription()));
        } catch (IOException | ImageProcessingException e) {
            logger.error("An error occured : " + e.getMessage());
            throw new BadGeolocationException();
        }
    }

    @Override
    public void withinBounds(Point target, Point pointA, Point pointB) throws BadGeolocationException {
        if (checkCoordinateOut(target.getLatitude(), pointA.getLatitude(), pointB.getLatitude()) ||
                checkCoordinateOut(target.getLongitude(), pointA.getLongitude(), pointB.getLongitude())) {
            logger.error("Incorrect geolocation information");
            throw new BadGeolocationException();
        }
    }

    private boolean checkCoordinateOut(Double ref, Double ref1, Double ref2) {
        if ((ref >= ref1 && ref <= ref2) || (ref >= ref2 && ref <= ref1)) {
            return false;
        }
        return true;
    }

    private Double resolveCoordinateAsDecimal(String coordinate) {
        String[] split = coordinate.split(" ");
        List<Double> cleanCoordinates = Stream.of(split)
                .map(d -> Double.valueOf(d.replaceAll("[^\\d.]", "")))
                .collect(Collectors.toList());
        return cleanCoordinates.get(0) + (((cleanCoordinates.get(1) * 60) + cleanCoordinates.get(2)) / (60*60));
    }
}
