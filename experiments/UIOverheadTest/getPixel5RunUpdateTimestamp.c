#include <stdio.h>
#include <string.h>

#define MAX_PER_LINE 1024
#define MAX_LINE 1024

int main(void)
{
    char line[MAX_PER_LINE];
    char* token;
    FILE * pixel5RunUpdateLog = fopen("/home/ywha/WEB_FLUID/experiments/UIOverheadTest/pixel5RunUpdateLog.log","rt");
    for(int i = 0; i< MAX_LINE; i++)
    {
        fgets(line,MAX_PER_LINE,pixel5RunUpdateLog);
        if(line == NULL)
            break;
        if(i == 0 || i == 1 || i == 3 || i == 6 || i == 10 || i == 15)
        {
            strtok(line,"=");
            token = strtok(NULL,"=");
            printf("%s",token);
        }

    }
}