/*
 * SPDX-FileCopyrightText: 2021 Atos, 2024 INFO.nl
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { FormioForm } from "@formio/angular";
import { FormulierDefinitie } from "../../admin/model/formulieren/formulier-definitie";
import { GeneratedType } from "../../shared/utils/generated-types";
import { Taakinformatie } from "./taakinformatie";

/**
 * @deprecated - use the `GeneratedType`
 */
export class Taak {
  id: string;
  naam: string;
  toelichting: string;
  creatiedatumTijd: string;
  toekenningsdatumTijd: string;
  fataledatum: string;
  behandelaar?: GeneratedType<"RestUser">;
  groep: GeneratedType<"RestGroup">;
  zaakUuid: string;
  zaakIdentificatie: string;
  zaaktypeOmschrijving: string;
  status: GeneratedType<"TaakStatus">;
  formulierDefinitieId: GeneratedType<"FormulierDefinitie">;
  formulierDefinitie: FormulierDefinitie;
  formioFormulier: FormioForm;
  tabellen: Record<string, string[]>;
  taakdata: Record<string, string>;
  taakinformatie: Taakinformatie;
  taakdocumenten: string[];
  rechten: GeneratedType<"RestTaakRechten">;
  zaaktypeUUID: string;
}
