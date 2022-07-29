import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'daysToYears'
})
export class DaysToYearsPipe implements PipeTransform {

  transform(value: number): string {

    let result: string

    if (value > 0) {

      let years: number = Math.floor(value / 365)
      if (years >= 100) {
        result = "Non Scade"
      } else {
        let days: number = value - years * 365

        if (years == 0) result = days + " giorni"
        else if (years == 1) result = years + " anno e " + days + " giorni"
        else result = years + " anni e " + days + " giorni"
      }

    } else {

      result = "Scaduto"

    }

    return result

  }
}