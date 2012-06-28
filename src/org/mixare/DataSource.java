package org.mixare;

import java.util.List;


/**
 * This abstract class should be extended for new data sources.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public abstract class DataSource {
	public abstract List<Marker> getMarkers();
}
