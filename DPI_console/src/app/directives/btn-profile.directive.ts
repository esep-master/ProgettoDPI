import { Directive, ElementRef, Input } from '@angular/core';
import { Util } from '../utility/util';

@Directive({
  selector: '[btnProfile]'
})
export class BtnProfileDirective {

  @Input('btnProfile') btnProfile: string

  constructor(private elRef: ElementRef) {}

  ngOnInit() {

    let permessi: string[] = Util.getPermessi()

    if (this.btnProfile && !permessi.includes(this.btnProfile)) {
      this.elRef.nativeElement.style.display = "none"
    }

  }
}