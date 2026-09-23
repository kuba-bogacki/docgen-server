package com.company.controller;

import com.company.model.dto.CompanyDto;
import com.company.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.company.util.ApplicationConstants.API_VERSION;
import static com.company.util.ApplicationConstants.USER_EMAIL_HEADER;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = API_VERSION + "/company")
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping(value = "/exist/{companyKrsNumber}")
    public ResponseEntity<?> checkIfCompanyAlreadyExist(@PathVariable String companyKrsNumber) {
        return new ResponseEntity<>(companyService.checkIfCompanyAlreadyExist(companyKrsNumber), HttpStatus.OK);
    }

    @GetMapping(value = "/{companyName}")
    public ResponseEntity<?> getCompanyByCompanyName(@PathVariable String companyName) {
        return new ResponseEntity<>(companyService.getCompanyByName(companyName), HttpStatus.OK);
    }

    @GetMapping(value = "/details/{companyId}")
    public ResponseEntity<?> getCompanyByCompanyId(@PathVariable String companyId) {
        return new ResponseEntity<>(companyService.getCompanyByCompanyId(companyId), HttpStatus.OK);
    }

    @GetMapping(value = "/get-by-krs/{krsNumber}")
    public ResponseEntity<?> getCompanyByCompanyKrsNumber(@PathVariable String krsNumber) {
        return new ResponseEntity<>(companyService.getCompanyByCompanyKrsNumber(krsNumber), HttpStatus.OK);
    }

    @PostMapping(value = "/create")
    public ResponseEntity<?> createCompany(@RequestBody CompanyDto companyDto, @RequestHeader(USER_EMAIL_HEADER) String userEmail) {
        return new ResponseEntity<>(companyService.createCompany(companyDto, userEmail), HttpStatus.CREATED);
    }

    @PutMapping(value = "/update")
    public ResponseEntity<?> updateCompany(@RequestBody CompanyDto companyDto) {
        return new ResponseEntity<>(companyService.updateCompany(companyDto), HttpStatus.OK);
    }

    @GetMapping(value = "/current-user-companies")
    public ResponseEntity<?> getAllCompanies(@RequestHeader(USER_EMAIL_HEADER) String userEmail) {
        return new ResponseEntity<>(companyService.getCurrentUserCompanies(userEmail), HttpStatus.OK);
    }

    @GetMapping(value = "/company-members/{companyId}")
    public ResponseEntity<?> getCompanyMemberIdList(@PathVariable String companyId) {
        return new ResponseEntity<>(companyService.getCompanyMemberIdList(companyId), HttpStatus.OK);
    }

    @GetMapping(value = "/members-details/{companyId}")
    public ResponseEntity<?> getDetailMembersList(@PathVariable String companyId, @RequestHeader(USER_EMAIL_HEADER) String userEmail) {
        return new ResponseEntity<>(companyService.getDetailMembersList(companyId, userEmail), HttpStatus.OK);
    }

    @PutMapping(value = "/add-new-member/{companyId}")
    public ResponseEntity<?> addNewMemberToCompany(@PathVariable String companyId, @RequestBody String userId) {
        companyService.addNewMemberToCompany(companyId, userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
