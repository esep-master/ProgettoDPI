import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { MenuItem } from 'primeng/api';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  logo: any = ""

  constructor(public translate: TranslateService) {
  }

  ngOnInit(): void {
    this.getLogoImg()
  }

  getLogoImg() {
    //TODO chiamata a servizio che restituisce un base64
    this.logo = "data:image/png;base64," + "cio' che viene restiruito dal servizio"
    this.logo = "assets/Images/logoEasy.png"
  }

}
