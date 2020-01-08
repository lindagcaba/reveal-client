package org.smartregister.reveal.contract;

import org.smartregister.domain.Location;
import org.smartregister.reveal.model.OfflineMapModel;

import java.util.List;

public interface AvailableOfflineMapsContract {

    interface Presenter {

        void onDownloadMap();

        void onDownloadAreaSelected(OfflineMapModel offlineMapModel);

        void fetchOperationalAreas();

        void onFetchOperationalAreas(List<Location> locations);

    }

    interface View {

        void setOfflineMapModelList(List<OfflineMapModel> offlineMapModelList);

        void displayToast(String message);

        void displayError(int title, String message);

        void updateOperationalAreasToDownload(Location operationalAreasToDownload);
    }

    interface Interactor {

        void fetchOperationalAreas();
    }
}
