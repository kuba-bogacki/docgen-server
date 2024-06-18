package com.company.controller;

import com.company.exception.CompanyNonExistException;
import com.company.model.dto.CompanyDto;
import com.company.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.company.util.ApplicationConstants.API_VERSION;

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
        try {
            return new ResponseEntity<>(companyService.getCompanyByName(companyName), HttpStatus.OK);
        } catch (CompanyNonExistException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/details/{companyId}")
    public ResponseEntity<?> getCompanyByCompanyId(@PathVariable String companyId) {
        try {
            return new ResponseEntity<>(companyService.getCompanyByCompanyId(companyId), HttpStatus.OK);
        } catch (CompanyNonExistException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/get-by-krs/{krsNumber}")
    public ResponseEntity<?> getCompanyByCompanyKrsNumber(@PathVariable String krsNumber) {
        try {
            return new ResponseEntity<>(companyService.getCompanyByCompanyKrsNumber(krsNumber), HttpStatus.OK);
        } catch (CompanyNonExistException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/create")
    public ResponseEntity<?> createCompany(@RequestBody CompanyDto companyDto, @RequestHeader("Authorization") String jwtToken) {
        try {
            return new ResponseEntity<>(companyService.createCompany(companyDto, jwtToken), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @PutMapping(value = "/update")
    public ResponseEntity<?> updateCompany(@RequestBody CompanyDto companyDto) {
        try {
            return new ResponseEntity<>(companyService.updateCompany(companyDto), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/current-user-companies")
    public ResponseEntity<?> getAllCompanies(@RequestHeader("Authorization") String jwtToken) {
        try {
            return new ResponseEntity<>(companyService.getCurrentUserCompanies(jwtToken), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @GetMapping(value = "/company-members/{companyId}")
    public ResponseEntity<?> getCompanyMemberIdList(@PathVariable("companyId") String companyId) {
        try {
            return new ResponseEntity<>(companyService.getCompanyMemberIdList(companyId), HttpStatus.OK);
        } catch (CompanyNonExistException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/members-details/{companyId}")
    public ResponseEntity<?> getDetailMembersList(@PathVariable("companyId") String companyId, @RequestHeader("Authorization") String jwtToken) {
        try {
            return new ResponseEntity<>(companyService.getDetailMembersList(companyId, jwtToken), HttpStatus.OK);
        } catch (CompanyNonExistException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value = "/add-new-member/{companyId}")
    public ResponseEntity<?> addNewMemberToCompany(@PathVariable("companyId") String companyId, @RequestBody String userId) {
        try {
            companyService.addNewMemberToCompany(companyId, userId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
