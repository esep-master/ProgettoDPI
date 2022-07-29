import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'formatDate'
})
export class FormatDatePipe implements PipeTransform {

  transform(value: string): string {

    if (!!value) { // Controllo se la data è presente

      if (this.isValidDate(value)) { // Controllo se la data è valida

        let date: Date = new Date(value)
  
        let dateTimeFormat = new Intl.DateTimeFormat("it" , {
          day: '2-digit',
          month: '2-digit',
          year: 'numeric',
          hour: '2-digit',
          minute: '2-digit',
          second: '2-digit'
        })
  
        return dateTimeFormat.format(date)
  
      } else return "Data non valida"

    } else return ""
  }

  isValidDate(dateString: string): boolean {
    return !isNaN(new Date(dateString).getDate())
  }
}