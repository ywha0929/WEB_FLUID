#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#define DATA_SIZE 1000
#define MAX_PER_LINE 1024


int main(void)
{
    char line[1024];
    char* token;
    char* frameComplete;
    int frameCompleteIndex = 13;
    int count=0;
    FILE * pixel4aDumpsys = fopen("/home/ywha/WEB_FLUID/experiments/UIOverheadTest/react_proxy_framestats.log","rt");
    fgets(line,MAX_PER_LINE,pixel4aDumpsys);
    // printf("%s\n",line);
    for(token = strtok(line,","); token != NULL; token = strtok(NULL,","))
    {
        // printf("%s\n",token);
        
        if(count == frameCompleteIndex)
        {
            frameComplete = token;
            break;
        }
        count++;

    }
    printf("%s\n",frameComplete);
}