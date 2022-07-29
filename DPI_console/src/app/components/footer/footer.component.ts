import { Component, OnInit } from '@angular/core';
import { VERSION } from 'src/app/utility/constants';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css']
})
export class FooterComponent implements OnInit {

  logo = "assets/Images/logoEasy.png"
  versione = VERSION

  constructor() { }

  ngOnInit(): void {
  }

}