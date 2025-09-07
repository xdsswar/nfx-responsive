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
 * Created on 09/06/2025 at 18:46
 */
module nfx.responsive {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;

    exports xss.it.nfx.responsive.control;
    exports xss.it.nfx.responsive.misc;
    exports xss.it.nfx.responsive.layout;
    opens xss.it.nfx.responsive.control;
    opens xss.it.nfx.responsive.misc;
    opens xss.it.nfx.responsive.layout;
}