package agh.ics.oop;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class Gene {
    /*class with animal genes, it determinate where animal will probably move*/
    int num_of_gen = 32;
    public int Genes_arr [] = new int[num_of_gen];
    public Gene(){
        for(int i=0; i<num_of_gen;i++)
            this.Genes_arr[i] = (int)(Math.random() * 8);
        Arrays.sort(this.Genes_arr);
    }

    public Gene(Animal ani1, Animal ani2){  //g1
        /*mix 2 animal genes to create child one */
        int sum = ani1.stamina + ani2.stamina;
        int ani1_gene_usage = ani1.stamina/sum;
        int ani2_gene_usage = ani2.stamina/sum;

        int l_or_r =ThreadLocalRandom.current().nextInt(0, 1 + 1);  //0 - lewo 1 - prawo

        if(l_or_r == 0){
            for(int i=0; i<(int)ani1_gene_usage*this.num_of_gen;i++)
                this.Genes_arr[i] = ani1.genes.Genes_arr[i];
            for(int i=(int)ani1_gene_usage*this.num_of_gen; i<num_of_gen;i++)
                this.Genes_arr[i] = ani2.genes.Genes_arr[i];
        }
        else{
            for(int i=0; i<(int)ani2_gene_usage*this.num_of_gen;i++)
                this.Genes_arr[i] = ani2.genes.Genes_arr[i];
            for(int i=(int)ani2_gene_usage*this.num_of_gen; i<num_of_gen;i++)
                this.Genes_arr[i] = ani1.genes.Genes_arr[i];
        }
        Arrays.sort(this.Genes_arr);
    }
    public String print_gene(){
        /*print genes to String*/
       return Arrays.toString(Genes_arr);
    }


}
