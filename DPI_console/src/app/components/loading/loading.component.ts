import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-loading',
  templateUrl: './loading.component.html',
  styleUrls: ['./loading.component.css']
})
export class LoadingComponent implements OnInit {

  @Input() isWaiting: boolean = false
  @Input() style: any = {width: "350px"}

  constructor() { }

  ngOnInit(): void {
  }

}
