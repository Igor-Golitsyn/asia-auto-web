package com.asia_auto;

import com.asia_auto.data.components.imageUtil.ImageLoader;
import com.asia_auto.data.components.imageUtil.ImageTemp;
import com.asia_auto.data.dao.ElementEntityDao;
import com.asia_auto.data.entity.MainElement;
import com.asia_auto.data.entity.MasterElement;
import org.zkoss.image.AImage;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.io.IOException;
import java.sql.Time;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Игорь on 31.10.2016.
 */
public class Model extends SelectorComposer<Component> {
    Logger logger = Logger.getLogger(Model.class.getName());
    private static final long serialVersionUID = 1L;
    private ElementEntityDao elementEntityDao = new ElementEntityDao();

    @Wire
    private Window win;
    @Wire
    private Vbox leftTab;
    @Wire
    private Vbox rightTab;

    private ListModel<MainElement> mainElementListModel;
    private Set<MasterElement> masters;

    public ListModel<MainElement> getMainElementListModel() {
        return mainElementListModel;
    }

    public Model() {
        logger.log(Level.INFO, "createModel");
        fullingMainElementList();
    }

    private void fullingMainElementList() {
        logger.log(Level.INFO, "fullingMainElementList");
        List<MainElement> elements = elementEntityDao.getMainForDate(new Date());
        Time currentTime = new Time(new Date().getTime());
        Iterator<MainElement> iterator = elements.iterator();
        while (iterator.hasNext()) {
            MainElement mainElement = iterator.next();
            String t1 = mainElement.getTime().toString().replaceAll(":", "");
            String t2 = currentTime.toString().substring(0, 5).replaceAll(":", "");
            int time1 = Integer.parseInt(t1);
            int time2 = Integer.parseInt(t2);
            if (time1 < time2) iterator.remove();
        }
        masters = new HashSet<>();
        elements.sort((o1, o2) -> {
            return o1.getTime().getTime().compareTo(o2.getTime().getTime());
        });
        for (MainElement mainEl : elements) {
            masters.add(mainEl.getMaster());
        }
        mainElementListModel = new ListModelList<MainElement>(elements);
    }

    @Listen("onCreate = #win")
    public void init() {
        logger.log(Level.INFO, "init");
        try {
            setMasters();
        } catch (IOException e) {
        }
    }

    private void setMasters() throws IOException {
        logger.log(Level.INFO, "setMasters");
        boolean left = true;
        int i = 0;
        for (MasterElement master : masters) {
            if (i++ > 3) continue;
            Image image = new Image();
            ImageTemp imageTemp = ImageLoader.fromBytes(master.getFoto());
            imageTemp = imageTemp.getResizedToSquare(300, 0);
            image.setContent(new AImage("1", imageTemp.getByteArray()));
            image.setHeight("300px");
            image.setWidth("300px");
            Label label1 = new Label(master.getName() + " " + master.getSecondName());
            Label label2 = new Label(master.getFamily());
            label1.setSclass("main-tabel");
            label2.setSclass("main-tabel");
            if (left) {
                leftTab.appendChild(image);
                leftTab.appendChild(label1);
                leftTab.appendChild(label2);
                left = false;
            } else {
                rightTab.appendChild(image);
                rightTab.appendChild(label1);
                rightTab.appendChild(label2);
                left = true;
            }
        }
    }
}
