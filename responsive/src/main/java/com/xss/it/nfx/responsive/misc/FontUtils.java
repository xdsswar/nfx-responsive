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

package com.xss.it.nfx.responsive.misc;

import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * nfx-responsive
 * <p>
 * Description:
 * This class is part of the com.xss.it.nfx.responsive.misc package.
 *
 * <p>
 *
 * @author XDSSWAR
 * @version 1.0
 * @since September 09, 2025
 * <p>
 * Created on 09/09/2025 at 23:43
 */
public final class FontUtils {

    private FontUtils() {}

    // ---------- Public entry ----------
    public static Font resolveFont(String familyOrFace, double size) {
        if (familyOrFace == null || familyOrFace.isBlank()) return Font.font(size);

        ParsedFontSpec spec = parseFontSpec(familyOrFace);
        String family = resolveInstalledFamily(spec.family);

        FontWeight weight  = (spec.weight  != null) ? spec.weight  : FontWeight.NORMAL;
        FontPosture posture = (spec.posture != null) ? spec.posture : FontPosture.REGULAR;

        // As a final guard: if family is still null/blank, fall back to default
        if (family == null || family.isBlank()) return Font.font(size);

        return Font.font(family, weight, posture, size);
    }

    // ---------- Parse "face" into family + style ----------
    private static final class ParsedFontSpec {
        final String family; final FontWeight weight; final FontPosture posture;
        ParsedFontSpec(String family, FontWeight weight, FontPosture posture) {
            this.family = family; this.weight = weight; this.posture = posture;
        }
    }

    private static ParsedFontSpec parseFontSpec(String s) {
        String orig = s.trim();
        String low  = orig.toLowerCase(Locale.ROOT);

        // posture
        FontPosture posture = (low.contains("italic") || low.contains("oblique"))
                ? FontPosture.ITALIC : FontPosture.REGULAR;

        // weight (ordered strongest-first to avoid collisions)
        FontWeight weight =
                low.contains("black")         ? FontWeight.BLACK :
                        low.contains("extra bold")    ? FontWeight.EXTRA_BOLD :
                                low.contains("extrabold")     ? FontWeight.EXTRA_BOLD :
                                        low.contains("ultra bold")    ? FontWeight.EXTRA_BOLD :
                                                low.contains("semi bold")     ? FontWeight.SEMI_BOLD :
                                                        low.contains("semibold")      ? FontWeight.SEMI_BOLD :
                                                                low.contains("demi bold")     ? FontWeight.SEMI_BOLD :
                                                                        low.contains("bold")          ? FontWeight.BOLD :
                                                                                low.contains("medium")        ? FontWeight.MEDIUM :
                                                                                        low.contains("extra light")   ? FontWeight.EXTRA_LIGHT :
                                                                                                low.contains("ultra light")   ? FontWeight.EXTRA_LIGHT :
                                                                                                        low.contains("extralight")    ? FontWeight.EXTRA_LIGHT :
                                                                                                                low.contains("light")         ? FontWeight.LIGHT :
                                                                                                                        low.contains("thin")          ? FontWeight.THIN :
                                                                                                                                null;

        // strip tokens to leave family candidate
        String family = orig
                .replaceAll("(?i)\\b(extra\\s*bold|ultra\\s*bold|extrabold|semi\\s*bold|semibold|demi\\s*bold|bold|medium|"
                        + "extra\\s*light|ultra\\s*light|extralight|light|thin)\\b", "")
                .replaceAll("(?i)\\b(italic|oblique)\\b", "")
                .replaceAll("[\\s\\-]{2,}", " ")
                .trim();
        if (family.isEmpty()) family = orig;

        return new ParsedFontSpec(family, weight, posture);
    }

    // ---------- Family resolution across families & faces ----------
    private static String resolveInstalledFamily(String requestedFamilyLike) {
        if (requestedFamilyLike == null || requestedFamilyLike.isBlank()) {
            return Font.getDefault().getFamily();
        }

        // Collect installed families and faces once per call
        List<String> families = Font.getFamilies();
        List<String> faces    = Font.getFontNames();

        String normReq = normalize(requestedFamilyLike);

        // 1) Direct case-insensitive family match
        for (String fam : families) {
            if (fam.equalsIgnoreCase(requestedFamilyLike)) return fam;
        }
        // 2) Normalized equality (ignore spaces, hyphens, underscores, case)
        for (String fam : families) {
            if (normalize(fam).equals(normReq)) return fam;
        }
        // 3) Face → family mapping: look for a face that matches, then map it back to its family
        Map<String, String> faceToFamily = buildFaceToFamilyMap(families);
        // exact face match
        for (String face : faces) {
            if (face.equalsIgnoreCase(requestedFamilyLike)) {
                String fam = faceToFamily.getOrDefault(face, null);
                if (fam != null) return fam;
            }
        }
        // normalized face match
        for (String face : faces) {
            if (normalize(face).equals(normReq)) {
                String fam = faceToFamily.getOrDefault(face, null);
                if (fam != null) return fam;
            }
        }
        // prefix face match (helps with things like "SegoeUI" vs "Segoe UI")
        for (String face : faces) {
            if (normalize(face).startsWith(normReq)) {
                String fam = faceToFamily.getOrDefault(face, null);
                if (fam != null) return fam;
            }
        }
        // 4) Prefix family match
        for (String fam : families) {
            if (normalize(fam).startsWith(normReq)) return fam;
        }

        // 5) Give JavaFX a shot with the raw string (may still resolve on some platforms)
        return requestedFamilyLike;
    }

    private static Map<String, String> buildFaceToFamilyMap(List<String> families) {
        Map<String, String> map = new HashMap<>(256);
        for (String fam : families) {
            // get all faces for this family; entries include "Family", "Family Bold", etc.
            for (String face : Font.getFontNames(fam)) {
                map.put(face, fam);
            }
        }
        return map;
    }

    private static String normalize(String s) {
        return s.toLowerCase(Locale.ROOT).replaceAll("[\\s\\-_/]+", "");
    }
}
