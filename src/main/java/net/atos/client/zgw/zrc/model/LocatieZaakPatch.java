/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc.model;


import jakarta.json.bind.annotation.JsonbNillable;
import jakarta.json.bind.annotation.JsonbProperty;

/**
 * Zaak locatie patch data
 */
public class LocatieZaakPatch extends Zaak {

    @JsonbProperty(nillable = true)
    private final Geometry zaakgeometrie;

    public LocatieZaakPatch(final Geometry zaakgeometrie) {
        this.zaakgeometrie = zaakgeometrie;
    }

    @JsonbNillable()
    public Geometry getZaakgeometrie() {
        return zaakgeometrie;
    }
}
