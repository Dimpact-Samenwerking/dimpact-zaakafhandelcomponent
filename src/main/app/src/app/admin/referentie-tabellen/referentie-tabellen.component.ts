/*
 * SPDX-FileCopyrightText: 2022 Atos, 2024 INFO.nl
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, OnInit, ViewChild } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MatSidenav, MatSidenavContainer } from "@angular/material/sidenav";
import { MatTableDataSource } from "@angular/material/table";
import { ConfiguratieService } from "../../configuratie/configuratie.service";
import { UtilService } from "../../core/service/util.service";
import {
  ConfirmDialogComponent,
  ConfirmDialogData,
} from "../../shared/confirm-dialog/confirm-dialog.component";
import { GeneratedType } from "../../shared/utils/generated-types";
import { AdminComponent } from "../admin/admin.component";
import { ReferentieTabelService } from "../referentie-tabel.service";

@Component({
  templateUrl: "./referentie-tabellen.component.html",
  styleUrls: ["./referentie-tabellen.component.less"],
})
export class ReferentieTabellenComponent
  extends AdminComponent
  implements OnInit
{
  @ViewChild("sideNavContainer") sideNavContainer: MatSidenavContainer;
  @ViewChild("menuSidenav") menuSidenav: MatSidenav;

  isLoadingResults = false;
  columns: string[] = ["code", "systeem", "naam", "waarden", "id"];
  dataSource = new MatTableDataSource<GeneratedType<"RestReferenceTable">>();

  constructor(
    public dialog: MatDialog,
    public utilService: UtilService,
    public configuratieService: ConfiguratieService,
    private service: ReferentieTabelService,
  ) {
    super(utilService, configuratieService);
  }

  ngOnInit(): void {
    this.setupMenu("title.referentietabellen");
    this.laadReferentieTabellen();
  }

  laadReferentieTabellen(): void {
    this.isLoadingResults = true;
    this.service.listReferentieTabellen().subscribe((tabellen) => {
      this.dataSource.data = tabellen;
      this.isLoadingResults = false;
    });
  }

  verwijderReferentieTabel(
    referentieTabel: GeneratedType<"RestReferenceTable">,
  ): void {
    this.dialog
      .open(ConfirmDialogComponent, {
        data: new ConfirmDialogData(
          {
            key: "msg.tabel.verwijderen.bevestigen",
            args: { tabel: referentieTabel.code },
          },
          this.service.deleteReferentieTabel(referentieTabel.id!),
        ),
      })
      .afterClosed()
      .subscribe((result) => {
        if (result) {
          this.utilService.openSnackbar("msg.tabel.verwijderen.uitgevoerd", {
            tabel: referentieTabel.code,
          });
          this.laadReferentieTabellen();
        }
      });
  }
}
