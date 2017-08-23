package fr.efaya.api;

import java.io.File;

public interface GeoLocService {

    Point extractGPSInformation(File file) throws BadGeolocationException;
    void withinBounds(Point target, Point pointA, Point pointB) throws BadGeolocationException;
}
