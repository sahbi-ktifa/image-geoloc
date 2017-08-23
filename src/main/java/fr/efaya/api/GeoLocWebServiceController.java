package fr.efaya.api;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by sktifa on 25/11/2016.
 */
@RestController
@CrossOrigin
@RequestMapping("api/geo")
public class GeoLocWebServiceController {

    @Autowired
    private GeoLocService geoLocService;

    @RequestMapping(value = "within", method = RequestMethod.POST)
    public void withinBounds(@RequestParam MultipartFile file,
                             @RequestParam Double latitudeA,
                             @RequestParam Double longitudeA,
                             @RequestParam Double latitudeB,
                             @RequestParam Double longitudeB) throws BadGeolocationException {
        if (file == null) {
            throw new BadGeolocationException();
        }
        File binary = null;
        try {
            binary = convert(file);
            String mimeType = new MimetypesFileTypeMap().getContentType(binary);
            if (mimeType == null || !mimeType.split("/")[0].equals("image")) {
                throw new BadGeolocationException();
            }
            Point coordinates = geoLocService.extractGPSInformation(binary);
            geoLocService.withinBounds(coordinates, new Point(latitudeA, longitudeA), new Point(latitudeB, longitudeB));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (binary != null) {
                FileUtils.deleteQuietly(binary);
            }
        }
    }

    private File convert(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
}
