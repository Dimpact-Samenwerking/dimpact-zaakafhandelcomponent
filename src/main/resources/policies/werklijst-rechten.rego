#
# SPDX-FileCopyrightText: 2024 Lifely
# SPDX-License-Identifier: EUPL-1.2+
#
# When updating this file, please make sure to also update the policy documentation
# in ~/docs/solution-architecture/accessControlPolicies.md
#
package net.atos.zac.werklijst

import future.keywords
import data.net.atos.zac.rol.behandelaar
import data.net.atos.zac.rol.beheerder
import data.net.atos.zac.rol.coordinator
import data.net.atos.zac.rol.recordmanager
import input.user

werklijst_rechten := {
    "inbox": inbox,
    "ontkoppelde_documenten_verwijderen": ontkoppelde_documenten_verwijderen,
    "inbox_productaanvragen_verwijderen": inbox_productaanvragen_verwijderen,
    "zaken_taken": zaken_taken,
    "zaken_taken_verdelen": zaken_taken_verdelen,
    "zaken_taken_exporteren": zaken_taken_exporteren
}

default inbox := false
inbox {
    behandelaar.rol in user.rollen
}

default ontkoppelde_documenten_verwijderen := false
ontkoppelde_documenten_verwijderen {
    recordmanager.rol in user.rollen
}

default inbox_productaanvragen_verwijderen := false
inbox_productaanvragen_verwijderen {
    recordmanager.rol in user.rollen
}

default zaken_taken := false
zaken_taken {
    behandelaar.rol in user.rollen
}

default zaken_taken_verdelen := false
zaken_taken_verdelen {
    coordinator.rol in user.rollen
}

default zaken_taken_exporteren := false
zaken_taken_exporteren {
    beheerder.rol in user.rollen
}
