/*
 * Copyright © 2025. XTREME SOFTWARE SOLUTIONS
 *
 * All rights reserved. Unauthorized use, reproduction, or distribution
 * of this software or any portion of it is strictly prohibited and may
 * result in severe civil and criminal penalties. This code is the sole
 * proprietary of XTREME SOFTWARE SOLUTIONS.
 *
 * Commercialization, redistribution, and use without explicit permission
 * from XTREME SOFTWARE SOLUTIONS, are expressly forbidden.
 */

/**
 * nfx-responsive
 * <p>
 * Description:
 * This class is part of the  package.
 *
 * <p>
 *
 * @author XDSSWAR
 * @since September 06, 2025
 * <p>
 * Created on 09/06/2025 at 22:17
 */
module nfx.demo {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires nfx.responsive;
    requires org.json;

    exports xss.it.demo;
    opens xss.it.demo;

    exports xss.it.demo.ctrl;
    opens xss.it.demo.ctrl;
    exports xss.it.demo.entity;
    opens xss.it.demo.entity;
    exports xss.it.demo.model;
    opens xss.it.demo.model;
}