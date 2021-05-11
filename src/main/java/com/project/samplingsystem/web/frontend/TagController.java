package com.project.samplingsystem.web.frontend;

import com.project.samplingsystem.dao.repository.TagRepository;
import com.project.samplingsystem.model.entity.NBTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * created by Wuwenbin on 2018/8/4 at 11:25
 *
 * @author wuwenbin
 */
@Controller
@RequestMapping("/tag")
public class TagController {

    private final TagRepository tagRepository;

    @Autowired
    public TagController(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @GetMapping("/all")
    @ResponseBody
    public List<NBTag> tagsList() {
        return tagRepository.findAll();
    }
}
