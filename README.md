
# Razredi

## Event
```java 
class Event {
    public int dayOfWeek; //Dan dogodka (0 = Ponedeljek, 4 = petek).
    public int beginWeek; //Teden, s katerim se predmet začne izvajati ( teden 1 se začne 1.10).
    public int endWeek; //Teden, do katerega se predmet izvaja.
    public String startTime; //Ura, ob kateri se začne dogodek.
    public String endTime; //Ura, ob kateri se konča dogodek.
    public int duration; //Trajanje dogodka v minutah.
    public String type; //Tip dogodka (Npr. vaje , predavanja , seminarske vaje...).
    public String course; //Naziv predmeta.
    public String room; //Lokacija dogodka.
    public String professor; // Naziv izvajalca.
    public Group group; 
    
    public class Group {  
	    public String field;  //Naziv smeri, ki ima ta predmet.
	    public long year;  //Letnik smeri, ki ima ta predmet.
	    public String type;  // Tip smeri (npr. VS, UNI).
	    public String subGroup; //Podskupina smeri s tem predmetom.
    }
   }
```

  ## Faculty
  ``` java
  class Faculty {  
    public String ShortName; //Kratica fakultete
    public String LongName; //Polno ime fakultete
 }
```
## GroupWYears
```java
class GroupWYears {  
    public String Name; //Naziv smeri
    public int[] Years; //Letniki smeri
}
```
## TinyDB
Podporni razred za hitro shranjevanje v SharedPreferences.

# Adapterji

## Adapter_Day
Adapter skrbi za prikazovanje dni dogodkov. Adapter prejme List Listov dogodkov, torej dogodke grupirane po dnevih iz pogleda  `Activity_View`, ter njihove datume.  Adapter vsak list v listu razbije na nov list listov, ki vsebuje dogodke grupirane po urah, ki jih pošlje v gnezden adapter `Adapter_DayContent`.
## Adapter_DayContent
Adapter prejme list listov dogodkov, v tem primeru dogodke grupirane po urah. Adapter je odgovoren za prikaz ur dogodkov. Odvisno od dolzine prejetega gnezdenega lista, adapter določi postavitev in obliko ure v dnevu.
## Adapter_SelectionPager
Adapter za tabbe v pogledu `Activity_Selection`.
# Pogledi
## Activity_About
## Activity_Selection
## Activity_View
## Tab_SelectCourse
## Tab_SelectProf
## Tab_SelectProgramme 

  
    

    
