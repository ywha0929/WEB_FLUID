#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_PER_LINE 1000

int main(void)
{
    FILE* src = fopen("/home/ywha/WEB_FLUID/experiments/finalRTTUpdate/file.txt","rt");
    FILE* output = fopen("/home/ywha/WEB_FLUID/experiments/finalRTTUpdate/output.csv","wt");
    char line[MAX_PER_LINE];

    for(int i = 0; i< 110; i++)
    {
        char * tok;
        fgets(line,MAX_PER_LINE,src);
        if(i%11 >=0 && i%11 < 4)
        {
            tok = strtok(line,":\n");
            tok = strtok(NULL,":\n");
            tok = strtok(NULL,":\n");
            tok = strtok(NULL,":\n");
            tok = strtok(NULL,":\n");

            fprintf(output,"%s,",tok);
        }
        else if (i%11 >=4 && i%11 < 9)
        {
            tok = strtok(line,":\n");
            tok = strtok(NULL,":\n");
            tok = strtok(NULL,":\n");
            tok = strtok(NULL,":\n");
            tok = strtok(NULL,":\n");
            tok = strtok(NULL,":\n,");
            tok = strtok(NULL,":\n,");

            fprintf(output,"%s,",tok);
        }
        else if(i%11==9)
        {
            
        }
        else if(i%11==10)
        {
            fprintf(output,"%s",line);
        }
    }
}