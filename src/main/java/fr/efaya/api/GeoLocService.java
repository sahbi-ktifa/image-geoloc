package fr.efaya.api;

import java.io.File;

public interface GeoLocService {
    void verify(File file) throws BadGeolocationException;
}
