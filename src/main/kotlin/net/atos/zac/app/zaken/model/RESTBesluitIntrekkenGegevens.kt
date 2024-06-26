/*
 * SPDX-FileCopyrightText: 2023 Atos, 2024 Lifely
 * SPDX-License-Identifier: EUPL-1.2+
 */
package net.atos.zac.app.zaken.model

import nl.lifely.zac.util.AllOpen
import nl.lifely.zac.util.NoArgConstructor
import java.time.LocalDate
import java.util.UUID

@AllOpen
@NoArgConstructor
data class RESTBesluitIntrekkenGegevens(
    var besluitUuid: UUID? = null,

    var vervaldatum: LocalDate? = null,

    var vervalreden: String? = null,

    var reden: String? = null
)
