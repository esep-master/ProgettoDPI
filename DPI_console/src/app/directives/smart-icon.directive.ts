import { Directive, ElementRef, Input } from '@angular/core';

@Directive({
  selector: '[smartIcon]'
})
export class SmartIconDirective {

  @Input('smartIcon') smartIcon: string

  constructor(private elRef: ElementRef) {}

  ngAfterViewInit() {
    this.elRef.nativeElement.src = "assets/IconSmartDPI/" + this.smartIcon + ".png"
  }
}