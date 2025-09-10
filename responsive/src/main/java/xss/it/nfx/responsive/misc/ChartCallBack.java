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

package xss.it.nfx.responsive.misc;

/**
 * nfx-responsive
 * <p>
 * Description:
 * This class is part of the xss.it.nfx.responsive.misc package.
 *
 * <p>
 *
 * @author XDSSWAR
 * @version 1.0
 * @since September 10, 2025
 * <p>
 * Created on 09/10/2025 at 11:36
 * <p>
 * Generic callback contract used by chart components to produce a result from input data
 * within a given owner/context.
 *
 * <p>Typical uses include building popup content, formatting labels, or computing styles
 * for a specific datum in the context of a chart/control.</p>
 *
 * @param <DATA> the input data type (e.g., a datum or model object)
 * @param <RESULT> the result type produced by the callback (e.g., a node, text, or style)
 * @param <CHART> the owner/context type invoking the callback (e.g., a chart, series, or node)
 */
@FunctionalInterface
public interface ChartCallBack<DATA, RESULT, CHART> {

    /**
     * Produces a result for the given data item in the specified owner/context.
     *
     * @param data  the input data
     * @param chart the owner or contextual object invoking this callback
     * @return the computed/created result
     */
    RESULT call(DATA data, CHART chart);
}
