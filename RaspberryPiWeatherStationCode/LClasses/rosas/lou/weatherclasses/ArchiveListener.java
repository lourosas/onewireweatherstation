/**
*/

package rosas.lou.weatherclasses;

import java.lang.*;
import java.util.*;
import rosas.lou.weatherclasses.ArchiveEvent;

public interface ArchiveListener{
   public void onAllDataSaveEvent(ArchiveEvent event);
   public void onDewpointSaveEvent(ArchiveEvent event);
   public void onHeatIndexSaveEvent(ArchiveEvent event);
   public void onHumiditySaveEvent(ArchiveEvent event);
   public void onTemperatureSaveEvent(ArchiveEvent event);
   public void onSaveEvent(ArchiveEvent event);
}
