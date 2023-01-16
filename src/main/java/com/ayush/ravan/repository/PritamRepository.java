package com.ayush.ravan.repository;

import com.ayush.ravan.exceptions.InvalidRequestException;
import com.ayush.ravan.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class PritamRepository {
    private static Map<Long,String> data = new HashMap<Long, String>() ;

    static {
        data.put(10l,"ayushamn");
        data.put(11l,"ravan");
        data.put(12l,"sona");
    }

    public String getCommentById(Long commentId) throws ResourceNotFoundException {
        this.checkRequest(commentId);
        return data.get(commentId);
    }

    public void deleteCommentById(Long commentId) throws ResourceNotFoundException {
        this.checkRequest(commentId);
        data.remove(commentId);
    }

    private void checkRequest(Long commentId) throws ResourceNotFoundException {
        if (commentId == null) {
            throw new InvalidRequestException("Comment ID must not be null!");
        }
        if (!data.containsKey(commentId)) {
            throw new ResourceNotFoundException("Comment with ID " + commentId + " does not exist.");
        }
    }
}
