package com.github.signer4j.task.imp;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.signer4j.imp.Args;
import com.github.signer4j.progress.IProgress;
import com.github.signer4j.progress.IProgressFactory;
import com.github.signer4j.progress.IStage;
import com.github.signer4j.task.IRequestResolver;
import com.github.signer4j.task.ITask;
import com.github.signer4j.task.ITaskRequest;
import com.github.signer4j.task.ITaskRequestExecutor;
import com.github.signer4j.task.ITaskResponse;
import com.github.signer4j.task.exception.TaskException;
import com.github.signer4j.task.exception.TaskExecutorException;
import com.github.signer4j.task.exception.TaskResolverException;

public class TaskRequestExecutor<I, O, R extends ITaskRequest<O>> implements ITaskRequestExecutor<I, O> {

  protected static final Logger LOGGER = LoggerFactory.getLogger(TaskRequestExecutor.class);
  
  private final IProgressFactory factory;

  private final IRequestResolver<I, O, R> requestResolver;

  private static enum Stage implements IStage {
    REQUEST_HANDLING("Tratando requisição"),
    
    PROCESSING_TASK("Processando a tarefa");

    private final String message;

    Stage(String message) {
      this.message = message;
    }

    @Override
    public final String toString() {
      return message;
    }

    @Override
    public final String interval() {
      return "(" + (ordinal() + 1) + " de 2)";
    }
  };
  
  protected TaskRequestExecutor(IRequestResolver<I, O, R> resolver, IProgressFactory factory) {
    this.requestResolver = Args.requireNonNull(resolver, "resolver is null");
    this.factory = Args.requireNonNull(factory, "factory is null");
  }
  
  @Override
  public final void execute(I request, O response) throws TaskExecutorException {
    try {
      
      IProgress progress = factory.get(); 
      
      try {
        beginExecution(progress);
        
        progress.begin(Stage.REQUEST_HANDLING, 2);
        progress.step("Resolvendo URL");
        R taskRequest;
        try {
          taskRequest = requestResolver.resolve(request);
        } catch (TaskResolverException e) {
          throw new TaskExecutorException("Não foi possível resolver a requisição", e);
        }
        progress.step("Notificando criação de requisção");
        
        onRequestResolved(taskRequest);
        
        progress.end();
        
        ITask<O> task = taskRequest.getTask(progress);
        
        try {
          progress.begin(Stage.PROCESSING_TASK, 2);
          progress.step("Iniciando a execução da tarefa '%s'", task.getId());
          ITaskResponse<O> output = task.get();
          progress.step("Processando resultados.");
        
          try {
            output.processResponse(response);
          } catch (IOException e) {
            LOGGER.warn("Exceção no processamento da resposta", e);
            progress.abort(e);
            return;
          }
          
          progress.end();
        } finally {
          task.dispose();
        }
          
      }catch(Exception e) {
        LOGGER.warn("Exceção na execução da requisição", e);
        progress.abort(e);
      }finally {
        endExecution(progress);
      }
    }catch(Throwable e) {
      throw new TaskExecutorException("Exceção inesperada na execução da tarefa", e);
    }
  }

  protected void onRequestResolved(R taskRequest) {
  }

  protected void beginExecution(IProgress progress) {
  }

  protected void endExecution(IProgress progress) {
    progress.stackTracer(s -> LOGGER.info(s.toString()));
    progress.dispose();
  }
}



