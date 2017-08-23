package fr.efaya.api;

import org.junit.Test;

public class GeoLocServiceImplTest {
    private GeoLocService service = new GeoLocServiceImpl();

    @Test(expected = BadGeolocationException.class)
    public void outBounds1() throws Exception {
        service.withinBounds(new Point(48, 1.7),
                new Point(48.87, 1.76),
                new Point(48.85, 1.81));
    }

    @Test(expected = BadGeolocationException.class)
    public void outBounds2() throws Exception {
        service.withinBounds(new Point(48.85, 1.7),
                new Point(48.87, 1.76),
                new Point(48.85, 1.81));
    }

    @Test(expected = BadGeolocationException.class)
    public void outBounds3() throws Exception {
        service.withinBounds(new Point(48.8, 1.77),
                new Point(48.87, 1.76),
                new Point(48.85, 1.81));
    }

    @Test
    public void withinBounds1() throws Exception {
        service.withinBounds(new Point(48.86, 1.77),
                new Point(48.87, 1.76),
                new Point(48.85, 1.81));
    }

    @Test
    public void withinBounds2() throws Exception {
        service.withinBounds(new Point(48.86, 1.77),
                new Point(48.85, 1.81),
                new Point(48.87, 1.76));
    }

}