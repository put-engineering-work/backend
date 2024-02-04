package work.service.util;

import org.springframework.stereotype.Service;
import work.domain.EventCategory;
import work.repository.CategoryRepository;

import java.util.ArrayList;

@Service
public class InitializerBean {
    private final CategoryRepository categoryRepository;

    public InitializerBean(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public void createCategoriesIfNotExist(){
        var name="SPORTS";
        var categoriesFromDB=categoryRepository.findByNameContains(name);
        if(categoriesFromDB==null|| categoriesFromDB.isEmpty()){
            var categories=new ArrayList<EventCategory>();
            categories.add(new EventCategory("SPORTS"));
            categories.add(new EventCategory("ART"));
            categories.add(new EventCategory("TECHNOLOGY"));
            categories.add(new EventCategory("SCIENCE"));
            categories.add(new EventCategory("MUSIC"));
            categories.add(new EventCategory("FASHION"));
            categories.add(new EventCategory("FOOD"));
            categories.add(new EventCategory("TRAVEL"));
            categories.add(new EventCategory("EXPOS"));
            categoryRepository.saveAll(categories);
        }

    }
}
