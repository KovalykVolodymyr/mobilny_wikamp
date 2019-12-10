package com.example.mobilny.wikamp;


import ezvcard.Ezvcard;
import ezvcard.VCard;
import ezvcard.VCardVersion;
import ezvcard.property.Revision;
import ezvcard.property.StructuredName;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class EmployeeWeeia {

    @GetMapping("/")
    public String main(Model model) {
        FindEmploee findEmployee = new FindEmploee();
        findEmployee.setName("");
        model.addAttribute("findEmployee", findEmployee);
        return "main";
    }

    @RequestMapping(value = "/person", method = RequestMethod.POST)
    public String search(@ModelAttribute FindEmploee findEmployee, Model model) throws IOException {
        model.addAttribute("findInputEmployee", findEmployee);
        String calendarEndpoint="https://adm.edu.p.lodz.pl/user/users.php?search=" + findEmployee.name;

        Document document = Jsoup.connect(calendarEndpoint).get();
        List<Employee> teachers = new ArrayList<>();

        Elements segment = document.select("div.user-info");
        for (Element element : segment) {
            Employee person = new Employee();

            if (!element.select("h3").text().equals("")) {
                person.setName(element.select("h3").text());
            }
            if (!element.select("h4").text().equals("")) {
                person.setTitle(element.select("h4").text());
            }
            if (!element.select("span.item-content").text().equals("")) {
                person.setWorkingPlace(element.select("span.item-content").text());
            }

            teachers.add(person);
        }

        model.addAttribute("employees", teachers);
        return "weeiaPeople";
    }

    private Employee fromString(String string) {
        Employee person = new Employee();

        String[] splitted = string.split("&");
        String name = splitted[0].replace("{", "").replace("}", "");
        person.setName(name);

        String title = splitted[1].replace("{", "").replace("}", "");
        if (!title.equals("null")) {
            person.setTitle(title);
        }

        String workingPlace = splitted[2].replace("{", "").replace("}", "");
        person.setWorkingPlace(workingPlace);

        return person;
    }

    @RequestMapping(value = "/person/{employee}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable String employee, Model model) throws IOException {
        VCard vcard = new VCard();

        Employee person = fromString(employee);

        StructuredName n = new StructuredName();
        n.setFamily(person.name.split(" ")[1]);
        n.setGiven(person.name.split(" ")[0]);
        if (person.title != null) {
            vcard.addTitle(person.title);
        }

        vcard.setFormattedName(person.getName());
        vcard.setRevision(Revision.now());



        File vcardFile = new File("employee.vcf");
        Ezvcard.write(vcard).version(VCardVersion.V4_0).go(vcardFile);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=employee.vcf");
        Resource fileSystemResource = new FileSystemResource("employee.vcf");
        return ResponseEntity.ok()
                .headers(headers)
                .body(fileSystemResource);
    }

}
