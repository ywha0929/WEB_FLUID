#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_PER_LINE 1000

int main(void)
{
    FILE* src = fopen("/home/ywha/WEB_FLUID/experiments/finalDistribute/file.txt","rt");
    FILE* output = fopen("/home/ywha/WEB_FLUID/experiments/finalDistribute/output.csv","wt");
    char line[MAX_PER_LINE];

    for(int i = 0; i< 60; i++)
    {
        char * tok;
        fgets(line,MAX_PER_LINE,src);
        if(i%6 >=0 && i%6 < 2)
        {
            tok = strtok(line,":\n");
            tok = strtok(NULL,":\n");
            tok = strtok(NULL,":\n");
            tok = strtok(NULL,":\n");
            tok = strtok(NULL,":\n");

            fprintf(output,"%s,",tok);
        }
        else if (i%6 >=2 && i%6 < 5)
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
        else if(i%6==5)
        {
            fprintf(output,"%s",line);
        }
    }
}