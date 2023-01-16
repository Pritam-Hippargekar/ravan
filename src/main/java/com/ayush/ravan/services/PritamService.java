package com.ayush.ravan.services;

import com.ayush.ravan.exceptions.ResourceNotFoundException;
import com.ayush.ravan.repository.PritamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class PritamService {

    @Autowired
    private PritamRepository pritamRepository;


   public String getCommentById(Long commentId) throws ResourceNotFoundException {
       return pritamRepository.getCommentById(commentId);
   }

    public void deleteCommentById(Long commentId) throws ResourceNotFoundException {
        pritamRepository.deleteCommentById(commentId);
    }

}
