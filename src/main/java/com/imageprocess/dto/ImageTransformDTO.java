package com.imageprocess.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageTransformDTO {
   private Transformations transformations;

   @Getter
   @Setter
    public static class Transformations{
        private Resize resize;
        private Crop crop;
        private int rotate;
        private String format;

        @Getter
        @Setter
        public static class Resize{
            private int width;
            private int height;
        }

        @Setter
        @Getter
       public static class Crop{
            private int width;
            private int height;
            private int x;
            private int y;
        }



    }
}
